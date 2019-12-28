using System;
using PT.Common;
using Newtonsoft.Json;

namespace PT.Feeder
{
  public class UdpModeRunner : IModeRunner
  {
    private IAppLogger _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(Program));
    private IFeeder _feeder = ServiceProvider.Get<IFeeder>();
    private IUdpSocket _udpSocket = ServiceProvider.Get<IUdpSocket>();

    public void Run(string[] args)
    {
      var parsedArgs = PTFeederArgs.ParseCommandLineArgs(args);

      _logger.GlobalConfig(parsedArgs.LogLevel);
      _feeder.Connect(parsedArgs.DeviceId);
      _udpSocket.InitializeServer(parsedArgs.UdpAddress, parsedArgs.UdpPort, UdpCallback);

      _logger.Info($"UDP Server Initialized on {parsedArgs.UdpAddress}:{parsedArgs.UdpPort}");
      while (Console.ReadLine().ToLower() != "exit")
      {
        continue;
      }
    }

    private void UdpCallback(string payload)
    {
      try
      {
        var inputReport = JsonConvert.DeserializeObject<InputReport>(payload);
        _feeder.Feed(inputReport);
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
    }
  }
}
