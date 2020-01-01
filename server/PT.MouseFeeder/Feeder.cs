
using System;
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
        return;
      }

      _previousMessageTimestamp = mouseReport.MessageTimestamp;

      Win32.POINT cursorPos;
      if (!Win32.GetCursorPos(out cursorPos))
      {
        _logger.Info("Unable to determine cursor position.");
        return;
      }

      // Determine the distance that will be moved.
      var velocity = mouseReport.Velocity ?? new Axis();
      var motion = new Axis();

      motion.X = GetDistanceMoved(velocity.X, _monitorInfo.rcMonitor.right);
      motion.Y = GetDistanceMoved(velocity.Y, _monitorInfo.rcMonitor.bottom);

      cursorPos.x += (int)motion.X;
      cursorPos.y += (int)motion.Y;

      if (cursorPos.x < 0)
      {
        cursorPos.x = 0;
      }
      else if (cursorPos.x > _monitorInfo.rcMonitor.right)
      {
        cursorPos.x = _monitorInfo.rcMonitor.right;
      }

      if (cursorPos.y < 0)
      {
        cursorPos.y = 0;
      }
      else if (cursorPos.y > _monitorInfo.rcMonitor.bottom)
      {
        cursorPos.y = _monitorInfo.rcMonitor.bottom;
      }

      Win32.SetCursorPos(cursorPos.x, cursorPos.y);

      int buttonsClicked = GetMouseClicks(mouseReport.Buttons);
      if (buttonsClicked > 0)
      {
        Win32.mouse_event((uint)buttonsClicked, (uint)cursorPos.x, (uint)cursorPos.y, 0, 0);
      }
    }

    public int GetMouseClicks(ButtonReport report)
    {
      int buttons = 0;
      if ((report.Buttons & Win32.DWORD_FLAGS.MOUSEEVENTF_LEFTDOWN) > 0)
      {
        buttons += Win32.DWORD_FLAGS.MOUSEEVENTF_LEFTDOWN;
      }
      else if ((report.Buttons & Win32.DWORD_FLAGS.MOUSEEVENTF_LEFTUP) > 0)
      {
        buttons += Win32.DWORD_FLAGS.MOUSEEVENTF_LEFTUP;
      }

      if ((report.Buttons & Win32.DWORD_FLAGS.MOUSEEVENTF_RIGHTDOWN) > 0)
      {
        buttons += Win32.DWORD_FLAGS.MOUSEEVENTF_RIGHTDOWN;
      }
      else if ((report.Buttons & Win32.DWORD_FLAGS.MOUSEEVENTF_RIGHTUP) > 0)
      {
        buttons += Win32.DWORD_FLAGS.MOUSEEVENTF_RIGHTUP;
      }

      return buttons;
    }

    public int GetDistanceMoved(float axis, long max)
    {
      float absAxis = Math.Abs(axis);
      int motion = 0;
      long speed = max / 4;

      // we want to ignore very minor movements of the axis so that
      // the mouse doesnt jitter around at the slightest motion.
      if (absAxis <= 0.1f)
      {
        return motion;
      }

      motion = (int)((absAxis - 0.1) * speed);

      // Overflow checking
      if (motion > speed || motion < 0)
      {
        motion = 0;
      }

      return axis >= 0 ? motion : -motion;
    }
  }
}