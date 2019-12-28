using System;
using PT.Common;
using PT.vJoyFeeder;

namespace PT.Feeder
{
  public class SampleModeRunner : IModeRunner
  {
    private static IAppLogger _logger;
    private static IFeeder _feeder;

    public void Run(string[] args)
    {
      _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(Program));
      _feeder = ServiceProvider.Get<IFeeder>();

      try
      {
        _feeder.Connect("1");
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
      Console.ReadKey();

      try
      {
        _feeder.Feed(new InputReport { Buttons = 4 });
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
      Console.ReadKey();
    }
  }
}
