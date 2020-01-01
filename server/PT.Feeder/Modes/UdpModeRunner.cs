using System;
using PT.Common;
using Newtonsoft.Json;
using System.Threading;

namespace PT.Feeder
{
  public class UdpModeRunner : IModeRunner
  {
    private IAppLogger _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(Program));
    private IInputFeeder _inputFeeder = ServiceProvider.Get<IInputFeeder>();
    private IMouseFeeder _mouseFeeder = ServiceProvider.Get<IMouseFeeder>();
    private IUdpSocket _udpSocket = ServiceProvider.Get<IUdpSocket>();

    public void Run(string[] args)
    {
      var parsedArgs = PTFeederArgs.ParseCommandLineArgs(args);

      _logger.GlobalConfig(parsedArgs.LogLevel);
      _inputFeeder.Connect(parsedArgs.DeviceId);
      _udpSocket.InitializeServer(parsedArgs.UdpAddress, parsedArgs.UdpPort, UdpCallback);

      _logger.Info($"UDP Server Initialized on {parsedArgs.UdpAddress}:{parsedArgs.UdpPort}");
      while (Console.ReadLine().ToLower() != "exit")
      {
        Thread.Sleep(1000);
        continue;
      }
    }

    private void UdpCallback(string payload)
    {
      try
      {
        var payloadObj = UdpPayload.Parse(payload);
        if (payloadObj.Status == PayloadStatus.Success)
        {
          switch (payloadObj.Type)
          {
            case PayloadType.Controller:
              HandleControllerPayload(payloadObj.Payload);
              break;
            case PayloadType.Mouse:
              HandleControllerPayload(payloadObj.Payload);
              break;
            default:
              _logger.Warn($"Unknown payload type: {payloadObj.Type.ToString()}");
              _logger.Debug($"Payload: {payload}");
              break;
          }
        }
        else
        {
          _logger.Warn($"Unable to parse incoming payload");
          _logger.Debug($"Payload: {payload}");
        }
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
    }

    private void HandleControllerPayload(string payload)
    {
      var inputReport = JsonConvert.DeserializeObject<InputReport>(payload);
      _inputFeeder.Feed(inputReport);
    }

    private void HandleMousePayload(string payload)
    {
      var mouseReport = JsonConvert.DeserializeObject<MouseReport>(payload);
      _mouseFeeder.Feed(mouseReport);
    }
  }
}
