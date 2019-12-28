using PT.Common;
using System;

namespace PT.Feeder
{
  internal class Logger : ILogger
  {
    public void Debug(string message)
    {
      Log(message, LogLevel.Debug);
    }

    public void Error(string message)
    {
      Log(message, LogLevel.Error);
    }

    public void Info(string message)
    {
      Log(message, LogLevel.Info);
    }

    public void Warn(string message)
    {
      Log(message, LogLevel.Warning);
    }

    public void Log(string message, LogLevel level)
    {
      Console.WriteLine(message);
    }
  }
}
