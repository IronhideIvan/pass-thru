using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public enum LogLevel
  {
    Error = 1,
    Warning = 2,
    Info = 3,
    Debug = 4
  }

  public interface IAppLogger
  {
    LogLevel LogLevel { get; }
    void Log(string message, LogLevel level);
    void Debug(string message);
    void Info(string message);
    void Warn(string message);
    void Error(string message);
    IAppLogger Configure(Type source);
    IAppLogger GlobalConfig(LogLevel level);
  }
}
