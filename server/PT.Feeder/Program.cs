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

      var feeder = ServiceProvider.Get<IFeeder>();
      feeder.Test();
      Console.ReadKey();
    }

    static void RegisterServices()
    {
      var provider = ServiceProvider.Instance;
      provider.RegisterSingleton<ILogger, Logger>();
      provider.RegisterSingleton<IFeeder, VJoyFeeder>();
    }
  }
}
