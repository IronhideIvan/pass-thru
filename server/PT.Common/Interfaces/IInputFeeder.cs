using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public interface IInputFeeder
  {
    void Connect(string deviceId);
    void Feed(InputReport inputReport);
  }
}
