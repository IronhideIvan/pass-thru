using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public interface IFeeder
  {
    void Connect(string deviceId);
  }
}
