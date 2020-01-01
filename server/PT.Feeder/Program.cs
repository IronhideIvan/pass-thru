using System;
using PT.Common;
using PT.vJoyFeeder;

namespace PT.Feeder
{
  class Program
  {
    static void Main(string[] args)
    {
      StartupModes mode = StartupModes.Sample;
      if (args != null && args.Length > 0)
      {
        mode = StartupModes.UDP;
      }
      RegisterServices();

      var logger = ServiceProvider.Get<IAppLogger>()
        .Configure(typeof(Program))
        .GlobalConfig(LogLevel.Debug);

      try
      {
        if (mode == StartupModes.Sample)
        {
          new SampleModeRunner().Run(args);
        }
        else
        {
          new UdpModeRunner().Run(args);
        }
      }
      catch (Exception ex)
      {
        logger.Error(ex.Message);
      }
    }

    static void RegisterServices()
    {
      var provider = ServiceProvider.Instance;
      provider.RegisterTransient<IAppLogger, ConsoleLogger>();
      provider.RegisterTransient<IInputFeeder, vJoyFeeder.Feeder>();
      provider.RegisterTransient<IUdpSocket, UdpSocket>();
      provider.RegisterTransient<IMouseFeeder, PT.MouseFeeder.Feeder>();
    }
  }
}
