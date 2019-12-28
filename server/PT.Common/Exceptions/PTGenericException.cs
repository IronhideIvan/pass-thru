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

    public PTGenericException(string message, Exception inner) : base(message, inner)
    {

    }
  }
}
