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

      var report = new InputReport
      {
        ButtonReport = new ButtonReport(),
        AxisReport = new AxisReport
        {
          Axis1 = new Axis(),
          Axis2 = new Axis()
        }
      };

      try
      {
        while (true)
        {
          report.ButtonReport.Buttons = 4;
          _feeder.Feed(report);
          System.Threading.Thread.Sleep(20);
          report.ButtonReport.Buttons = 0;
          _feeder.Feed(report);
          System.Threading.Thread.Sleep(20);
        }
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
      Console.ReadKey();
    }
  }
}
