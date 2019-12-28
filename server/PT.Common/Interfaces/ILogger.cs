using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public enum LogLevel
  {
    Debug = 1,
    Info = 2,
    Warning = 3,
    Error = 4
  }

  public interface ILogger
  {
    void Log(string message, LogLevel level);
    void Debug(string message);
    void Info(string message);
    void Warn(string message);
    void Error(string message);
    ILogger Configure(Type source);
  }
}
