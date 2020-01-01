using System;
using PT.Common;
using PT.vJoyFeeder;

namespace PT.Feeder
{
  public class SampleModeRunner : IModeRunner
  {
    private static IAppLogger _logger;

    public void Run(string[] args)
    {
      _logger = ServiceProvider.Get<IAppLogger>().Configure(typeof(Program));

      TestMouseFeeder();
    }

    private void TestInputFeeder()
    {
      var feeder = ServiceProvider.Get<IInputFeeder>();

      try
      {
        feeder.Connect("1");
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }

      var report = new InputReport
      {
        ButtonReport = new ButtonReport(),
        AxisReport = new InputAxisReport
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
          feeder.Feed(report);
          System.Threading.Thread.Sleep(20);
          report.ButtonReport.Buttons = 0;
          feeder.Feed(report);
          System.Threading.Thread.Sleep(20);
        }
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
      Console.ReadKey();
    }

    private void TestMouseFeeder()
    {
      var feeder = ServiceProvider.Get<IMouseFeeder>();

      try
      {
        feeder.Feed(new MouseReport());
      }
      catch (Exception ex)
      {
        _logger.Error(ex.Message);
      }
      Console.ReadKey();
    }
  }
}
