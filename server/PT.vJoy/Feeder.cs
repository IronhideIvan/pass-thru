using PT.Common;
using System;
using vJoyInterfaceWrap;
using Newtonsoft.Json;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.Threading;

namespace PT.vJoyFeeder
{
  public class Feeder : IInputFeeder
  {
    private static readonly IAppLogger _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(Feeder));
    private readonly ConcurrentQueue<InputReport> _backgroundQueue = new ConcurrentQueue<InputReport>();

    private vJoy _joystick;
    private uint _deviceId = 0;
    private Task _backgroundWorker;

    bool _axisX;
    long _axisMaxX;
    long _axisMinX;
    bool _axisY;
    long _axisMaxY;
    long _axisMinY;
    bool _axisZ;
    long _axisMaxZ;
    long _axisMinZ;
    bool _axisRX;
    long _axisMaxRX;
    long _axisMinRX;
    bool _axisRY;
    long _axisMaxRY;
    long _axisMinRY;
    bool _axisRZ;
    long _axisMaxRZ;
    long _axisMinRZ;
    int _nButtons;
    int _contPovNumber;
    int _discPovNumber;


    public void Connect(string deviceId)
    {
      // Validation.
      if (_joystick != null)
      {
        throw new PTGenericException($"Feeder is already connected to device ID '{_deviceId}'. Disconnect the feeder first before attempting a new connection.");
      }

      if (!UInt32.TryParse(deviceId, out _deviceId))
      {
        throw new PTGenericException($"Error parsing device ID '{deviceId}'. Must be a number.");
      }

      if (_deviceId < 1 || _deviceId > 16)
      {
        throw new PTGenericException($"Illegal device ID '{deviceId}'. Must be between 1 and 16, inclusive.");
      }

      // Initialize the joystick and position structure.
      _joystick = new vJoy();

      // Get the driver attributes (Vendor ID, Product ID, Version Number)
      if (!_joystick.vJoyEnabled())
      {
        throw new PTGenericException("vJoy driver not enabled: Failed Getting vJoy attributes.");
      }
      else
      {
        _logger.Debug($"Vendor: {_joystick.GetvJoyManufacturerString()}");
        _logger.Debug($"Product: {_joystick.GetvJoyProductString()}");
        _logger.Debug($"Version: {_joystick.GetvJoySerialNumberString()}");
      }

      // Test if DLL matches the driver
      uint dllVer = 0, drvVer = 0;
      bool isDriverMatch = _joystick.DriverMatch(ref dllVer, ref drvVer);
      if (isDriverMatch)
      {
        _logger.Debug($"Version of Driver Matches DLL Version ({dllVer})");
      }
      else
      {
        _logger.Warn($"Version of Driver ({drvVer}) does NOT match DLL Version ({dllVer})");
      }

      // Get the state of the requested device
      var status = _joystick.GetVJDStatus(_deviceId);
      switch (status)
      {
        case VjdStat.VJD_STAT_OWN:
          _logger.Debug($"vJoy Device '{_deviceId}' is already owned by this feeder.");
          break;
        case VjdStat.VJD_STAT_FREE:
          _logger.Debug($"vJoy Device '{_deviceId}' is free\n");
          break;
        case VjdStat.VJD_STAT_BUSY:
          throw new PTGenericException($"vJoy Device '{_deviceId}' is already owned by another feeder. Cannot connect.");
        case VjdStat.VJD_STAT_MISS:
          throw new PTGenericException($"vJoy Device '{_deviceId}' is not installed or disabled. Cannot connect.");
        default:
          throw new PTGenericException($"vJoy Device '{_deviceId}' general error. Cannot connect.");
      };

      // Acquire the target
      if ((status == VjdStat.VJD_STAT_OWN) || ((status == VjdStat.VJD_STAT_FREE) && (!_joystick.AcquireVJD(_deviceId))))
      {
        throw new PTGenericException($"Failed to acquire vJoy device number '{_deviceId}'.");
      }
      else
      {
        _logger.Info($"Acquired: vJoy device number '{_deviceId}'.");
      }

      _axisX = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_X);
      _axisY = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_Y);
      _axisZ = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_Z);
      _axisRX = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RX);
      _axisRY = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RY);
      _axisRZ = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RZ);
      // Get the number of buttons and POV Hat switchessupported by this vJoy device
      _nButtons = _joystick.GetVJDButtonNumber(_deviceId);
      _contPovNumber = _joystick.GetVJDContPovNumber(_deviceId);
      _discPovNumber = _joystick.GetVJDDiscPovNumber(_deviceId);

      _joystick.GetVJDAxisMin(_deviceId, HID_USAGES.HID_USAGE_X, ref _axisMinX);
      _joystick.GetVJDAxisMax(_deviceId, HID_USAGES.HID_USAGE_X, ref _axisMaxX);
      _joystick.GetVJDAxisMin(_deviceId, HID_USAGES.HID_USAGE_Y, ref _axisMinY);
      _joystick.GetVJDAxisMax(_deviceId, HID_USAGES.HID_USAGE_Y, ref _axisMaxY);
      _joystick.GetVJDAxisMin(_deviceId, HID_USAGES.HID_USAGE_Z, ref _axisMinZ);
      _joystick.GetVJDAxisMax(_deviceId, HID_USAGES.HID_USAGE_Z, ref _axisMaxZ);
      _joystick.GetVJDAxisMin(_deviceId, HID_USAGES.HID_USAGE_RX, ref _axisMinRX);
      _joystick.GetVJDAxisMax(_deviceId, HID_USAGES.HID_USAGE_RX, ref _axisMaxRX);
      _joystick.GetVJDAxisMin(_deviceId, HID_USAGES.HID_USAGE_RY, ref _axisMinRY);
      _joystick.GetVJDAxisMax(_deviceId, HID_USAGES.HID_USAGE_RY, ref _axisMaxRY);
      _joystick.GetVJDAxisMin(_deviceId, HID_USAGES.HID_USAGE_RZ, ref _axisMinRZ);
      _joystick.GetVJDAxisMax(_deviceId, HID_USAGES.HID_USAGE_RZ, ref _axisMaxRZ);

      PrintDeviceSupport();
    }

    public void Feed(InputReport genericReport)
    {
      // Validation
      if (_joystick == null || _deviceId < 1)
      {
        throw new PTGenericException("vJoy not initialized. Cannot feed inputs.");
      }

      if (_backgroundWorker == null)
      {
        _backgroundWorker = Task.Factory.StartNew(() => BackgroundWorker_DoWork());
      }

      if (_backgroundWorker.IsFaulted)
      {
        throw new PTGenericException(_backgroundWorker.Exception.Message);
      }

      _backgroundQueue.Enqueue(genericReport);
    }

    private Action BackgroundWorker_DoWork()
    {
      vJoy.JoystickState report = new vJoy.JoystickState();
      InputReport inputReport;
      long previousId = long.MinValue;
      while (true)
      {
        if (_backgroundQueue.TryDequeue(out inputReport))
        {
          if (inputReport == null || previousId > inputReport.MessageTimestamp)
          {
            // If we've already processed a message from a future point in time,
            // then discard this message as it's no longer valid.
            continue;
          }

          var buttonReport = inputReport.ButtonReport ?? new ButtonReport();
          var axisReport = inputReport.AxisReport ?? new InputAxisReport { Axis1 = new Axis(), Axis2 = new Axis() };

          if (_axisX)
          {
            report.AxisX = GetAxisValue(axisReport.Axis1.X, _axisMinX, _axisMaxX);
          }

          if (_axisY)
          {
            report.AxisY = GetAxisValue(axisReport.Axis1.Y, _axisMinY, _axisMaxY);
          }

          if (_axisZ)
          {
            report.AxisZ = GetAxisValue(axisReport.Axis1.Z, _axisMinZ, _axisMaxZ);
          }

          if (_axisRX)
          {
            report.AxisXRot = GetAxisValue(axisReport.Axis2.X, _axisMinRX, _axisMaxRX);
          }

          if (_axisRY)
          {
            report.AxisYRot = GetAxisValue(axisReport.Axis2.Y, _axisMinRY, _axisMaxRY);
          }

          if (_axisRZ)
          {
            report.AxisZRot = GetAxisValue(axisReport.Axis2.Z, _axisMinRZ, _axisMaxRZ);
          }

          report.Buttons = (uint)buttonReport.Buttons;

          if (!_joystick.UpdateVJD(_deviceId, ref report))
          {
            throw new PTGenericException($"Failed to feed vJoy device number '{_deviceId}'. Try reconnecting the device.");
          }

          Thread.Sleep(10);
        }
      }
    }

    private int GetAxisValue(float axis, long min, long max)
    {
      long median = (min + max) / 2;
      float absAxis = Math.Abs(axis);
      if (absAxis > 1.0)
      {
        absAxis = 1;
      }

      if (Math.Abs(axis) < 0.001)
      {
        return (int)median;
      }
      else if (axis < 0)
      {
        return (int)(median - (absAxis * median));
      }
      else
      {
        return (int)((absAxis * median) + median);
      }
    }

    private void PrintDeviceSupport()
    {
      string output = $"\nvJoy Device {_deviceId} capabilities:\n";
      output += $"Number of buttons\t\t{_nButtons}\n";
      output += $"Numner of Continuous POVs\t{_contPovNumber}\n";
      output += $"Numner of Descrete POVs\t\t{_discPovNumber}\n";
      output += $"Axis | Available | Min | Max";
      output += $"Axis X | {_axisX} | {_axisMinX} | {_axisMaxX} \n";
      output += $"Axis Y | {_axisY} | {_axisMinY} | {_axisMaxY} \n";
      output += $"Axis Z | {_axisZ} | {_axisMinZ} | {_axisMaxZ} \n";
      output += $"Axis Rx | {_axisRX} | {_axisMinRX} | {_axisMaxRX} \n";
      output += $"Axis Ry | {_axisRY} | {_axisMinRY} | {_axisMaxRY} \n";
      output += $"Axis Rz | {_axisRZ} | {_axisMinRZ} | {_axisMaxRZ} \n";

      _logger.Info(output);
    }
  }
}
