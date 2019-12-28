using PT.Common;
using System;
using vJoyInterfaceWrap;

namespace PT.vJoyFeeder
{
  public class VJoyFeeder : IFeeder
  {
    private static readonly IAppLogger _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(VJoyFeeder));

    private vJoy _joystick;
    private vJoy.JoystickState _report;
    private uint _deviceId = 0;

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
      _report = new vJoy.JoystickState();

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

      PrintDeviceSupport();
    }

    public void Feed(InputReport inputReport)
    {
      // Validation
      if (_joystick == null || _deviceId < 1)
      {
        throw new PTGenericException("vJoy not initialized. Cannot feed inputs.");
      }

      _report.Buttons = (uint)inputReport.Buttons;

      if (!_joystick.UpdateVJD(_deviceId, ref _report))
      {
        throw new PTGenericException($"Failed to feed vJoy device number '{_deviceId}'. Try reconnecting the device.");
      }
    }

    private void PrintDeviceSupport()
    {
      // Check which axes are supported
      bool AxisX = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_X);
      bool AxisY = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_Y);
      bool AxisZ = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_Z);
      bool AxisRX = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RX);
      bool AxisRZ = _joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RZ);
      // Get the number of buttons and POV Hat switchessupported by this vJoy device
      int nButtons = _joystick.GetVJDButtonNumber(_deviceId);
      int ContPovNumber = _joystick.GetVJDContPovNumber(_deviceId);
      int DiscPovNumber = _joystick.GetVJDDiscPovNumber(_deviceId);

      string output = $"\nvJoy Device {_deviceId} capabilities:\n";
      output += $"Number of buttons\t\t{_joystick.GetVJDButtonNumber(_deviceId)}\n";
      output += $"Numner of Continuous POVs\t{_joystick.GetVJDContPovNumber(_deviceId)}\n";
      output += $"Numner of Descrete POVs\t\t{_joystick.GetVJDDiscPovNumber(_deviceId)}\n";
      output += $"Axis X\t\t{_joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_X)}\n";
      output += $"Axis Y\t\t{_joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_Y)}\n";
      output += $"Axis Z\t\t{_joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_Z)}\n";
      output += $"Axis Rx\t\t{_joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RX)}\n";
      output += $"Axis Rz\t\t{_joystick.GetVJDAxisExist(_deviceId, HID_USAGES.HID_USAGE_RZ)}\n";

      _logger.Debug(output);
    }
  }
}
