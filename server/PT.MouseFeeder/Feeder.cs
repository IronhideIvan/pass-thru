
using System;
using System.Threading;
using System.Threading.Tasks;
using PT.Common;

namespace PT.MouseFeeder
{
  public class Feeder : IMouseFeeder
  {
    private static readonly IAppLogger _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(Feeder));
    private IntPtr _monitorHandle;
    private Win32.MONITORINFOEX _monitorInfo;
    private bool _isConnected = false;
    private long _previousMessageTimestamp;
    private Task _backgroundWorker;
    private MouseReport _mouseReport = new MouseReport();

    public void Connect()
    {
      _monitorHandle = Win32.MonitorFromPoint(new Win32.POINT(-10, -10), Win32.DWORD_FLAGS.MONITOR_DEFAULTTOPRIMARY);
      if (_monitorHandle == IntPtr.Zero)
      {
        throw new PTGenericException("Unable to determine primary monitor.");
      }

      _monitorInfo = new Win32.MONITORINFOEX();
      if (!Win32.GetMonitorInfo(_monitorHandle, _monitorInfo))
      {
        throw new PTGenericException("Unable to retrieve monitor information.");
      }

      _isConnected = true;
    }

    public void Feed(MouseReport mouseReport)
    {
      if (!_isConnected)
      {
        _logger.Info("Device not connected.");
      }

      // ignore any older messages
      // NOTE: These intentionally don't ignore messages with the same
      // timestamp.
      if (_previousMessageTimestamp > mouseReport.MessageTimestamp)
      {
        _logger.Debug("Discarding old message.");
        return;
      }

      _previousMessageTimestamp = mouseReport.MessageTimestamp;

      // Determine the distance that will be moved.
      _mouseReport = mouseReport;

      if (_backgroundWorker == null || _backgroundWorker.IsCompletedSuccessfully)
      {
        _backgroundWorker = Task.Factory.StartNew(() => BackgroundWorker_DoWork());
      }
      else if (_backgroundWorker.IsFaulted)
      {
        throw new PTGenericException(_backgroundWorker.Exception.Message);
      }
    }

    private void BackgroundWorker_DoWork()
    {
      // We will likely be looping over the same message multiple times
      // to move the cursor. So we need to make sure we don't hammer the
      // mouse buttons.
      long lastHandledMessageTimestamp = 0;
      Win32.RECT monitorBounds = new Win32.RECT
      {
        top = _monitorInfo.rcMonitor.top + 3,
        left = _monitorInfo.rcMonitor.left + 3,
        right = _monitorInfo.rcMonitor.right - 3,
        bottom = _monitorInfo.rcMonitor.bottom - 3
      };
      int refreshRate = 16;

      while (true)
      {
        var mouseReport = _mouseReport;
        var motion = new Axis();
        var velocity = mouseReport?.Velocity ?? new Axis();

        if (mouseReport == null)
        {
          break;
        }

        Win32.POINT cursorPos;
        if (!Win32.GetCursorPos(out cursorPos))
        {
          _logger.Info("Unable to determine cursor position.");
          return;
        }

        motion.X = GetDistanceMoved(velocity.X, Math.Abs(monitorBounds.right - monitorBounds.left), refreshRate);
        motion.Y = GetDistanceMoved(velocity.Y, Math.Abs(monitorBounds.top - monitorBounds.bottom), refreshRate);

        cursorPos.x += (int)motion.X;
        cursorPos.y += (int)motion.Y;

        if (cursorPos.x < monitorBounds.left)
        {
          cursorPos.x = monitorBounds.left;
        }
        else if (cursorPos.x > monitorBounds.right)
        {
          cursorPos.x = monitorBounds.right;
        }

        if (cursorPos.y < monitorBounds.top)
        {
          cursorPos.y = monitorBounds.top;
        }
        else if (cursorPos.y > monitorBounds.bottom)
        {
          cursorPos.y = monitorBounds.bottom;
        }

        Win32.SetCursorPos(cursorPos.x, cursorPos.y);

        if (mouseReport.Click && mouseReport.Buttons != null && mouseReport.MessageTimestamp > lastHandledMessageTimestamp)
        {
          int buttonsClicked = GetMouseClicks(mouseReport.Buttons);
          if (buttonsClicked > 0)
          {
            Win32.mouse_event((uint)buttonsClicked, (uint)cursorPos.x, (uint)cursorPos.y, 0, 0);
          }

          lastHandledMessageTimestamp = mouseReport.MessageTimestamp;
        }

        // If we've reached the max screen position, then don't worry about refreshing again.
        if ((cursorPos.x == monitorBounds.left || cursorPos.x == monitorBounds.right || motion.X == 0.0f)
          && (cursorPos.y == monitorBounds.top || cursorPos.y == monitorBounds.bottom || motion.Y == 0.0f))
        {
          break;
        }
        else
        {
          Thread.Sleep(refreshRate);
        }
      }
    }

    [Flags]
    private enum MouseButtons
    {
      None = 0,
      LeftClick = 256,
      RightClick = 131072
    }

    public int GetMouseClicks(ButtonReport report)
    {
      int buttons = 0;
      if ((report.Buttons & (int)MouseButtons.LeftClick) > 0)
      {
        buttons += Win32.DWORD_FLAGS.MOUSEEVENTF_LEFTDOWN;
      }
      else if ((report.Buttons & (int)MouseButtons.LeftClick) == 0)
      {
        buttons += Win32.DWORD_FLAGS.MOUSEEVENTF_LEFTUP;
      }

      return buttons;
    }

    public int GetDistanceMoved(float axisSpeed, long max, int timeMilliseconds)
    {
      if (axisSpeed == 0.0f)
      {
        return 0;
      }

      float absAxis = Math.Abs(axisSpeed);
      int motion = 0;
      int timeDelta = 1000 / timeMilliseconds;
      long maxSpeed = (max / 2) / timeDelta;

      // we want to ignore very minor movements of the axis so that
      // the mouse doesnt jitter around at the slightest motion.
      if (absAxis <= 0.1f)
      {
        return motion;
      }

      motion = (int)((absAxis - 0.1) * maxSpeed);

      // Overflow checking
      if (motion > maxSpeed || motion < 0)
      {
        motion = 0;
      }

      return axisSpeed > 0 ? motion : -motion;
    }
  }
}