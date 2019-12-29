using PT.Common;
using System;

namespace PT.Feeder
{
  internal class ConsoleLogger : IAppLogger
  {
    private static LogLevel _level = LogLevel.Error;
    private Type _source;

    public LogLevel LogLevel
    {
      get
      {
        return _level;
      }
    }

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
      if (level > _level)
      {
        return;
      }

      Console.WriteLine($"{DateTime.Now.TimeOfDay.ToString()} {level.ToString()}: {message}");
    }

    public IAppLogger Configure(Type source)
    {
      _source = source;
      return this;
    }

    public IAppLogger GlobalConfig(LogLevel level)
    {
      _level = level;
      return this;
    }
  }
}
