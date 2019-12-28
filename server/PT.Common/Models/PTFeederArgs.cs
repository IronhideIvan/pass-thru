using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public enum FeederTypes
  {
    vJoy = 1
  }

  public class PTFeederArgs
  {
    public FeederTypes Type { get; set; }
    public string DeviceId { get; set; }
    public LogLevel LogLevel { get; set; }
    public string UdpAddress { get; set; }
    public int UdpPort { get; set; }

    public static PTFeederArgs ParseCommandLineArgs(string[] args)
    {
      var obj = new PTFeederArgs();

      foreach (var a in args)
      {
        try
        {
          var splitArg = a.Split("=");

          switch (splitArg[0])
          {
            case "-t":
              obj.Type = Enum.Parse<FeederTypes>(splitArg[1]);
              break;
            case "-id":
              obj.DeviceId = splitArg[1].Replace("\"", "");
              break;
            case "-l":
              obj.LogLevel = Enum.Parse<LogLevel>(splitArg[1]);
              break;
            case "-a":
              obj.UdpAddress = splitArg[1].Replace("\"", "");
              break;
            case "-p":
              obj.UdpPort = Convert.ToInt32(splitArg[1]);
              break;
            default:
              break;
          }
        }
        catch (Exception ex)
        {
          throw new PTGenericException($"Exception encountered while parsing arg '{a}'", ex);
        }
      }

      return obj;
    }

    public string[] ToCommandLineArgs()
    {
      var args = new List<string>();
      args.Add($"-t={Convert.ToInt32(Type)}");
      args.Add($"-id=\"{DeviceId}\"");
      args.Add($"-l={Convert.ToInt32(LogLevel)}");
      args.Add($"-a=\"{UdpAddress}\"");
      args.Add($"-p={UdpPort}");
      return args.ToArray();
    }
  }
}
