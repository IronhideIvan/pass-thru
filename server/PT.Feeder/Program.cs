using System;
using PT.Common;
using PT.vJoyFeeder;

namespace PT.Feeder
{
  class Program
  {
    static void Main(string[] args)
    {
      RegisterServices();

      var logger = ServiceProvider.Get<ILogger>().Configure(typeof(Program));
      var feeder = ServiceProvider.Get<IFeeder>();
      try
      {
        feeder.Connect("1");
      }
      catch (Exception ex)
      {
        logger.Error(ex.Message);
      }
      Console.ReadKey();
    }

    static void RegisterServices()
    {
      var provider = ServiceProvider.Instance;
      provider.RegisterTransient<ILogger, ConsoleLogger>();
      provider.RegisterTransient<IFeeder, VJoyFeeder>();
    }
  }
}
