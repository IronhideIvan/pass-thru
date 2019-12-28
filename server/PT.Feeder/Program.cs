using System;
using PT.Common;
using PT.vJoy;

namespace PT.Feeder
{
  class Program
  {
    static void Main(string[] args)
    {
      RegisterServices();

      Console.WriteLine("Hello World!");
      Console.ReadKey();
    }

    static void RegisterServices()
    {
      var provider = ServiceProvider.Instance;
      provider.RegisterSingleton<IFeeder, VJoyFeeder>();
    }
  }
}
