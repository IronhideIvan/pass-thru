using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public class PTGenericException : Exception
  {
    public PTGenericException(string message) : base(message)
    {
    }
  }
}
