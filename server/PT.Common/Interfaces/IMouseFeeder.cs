using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public interface IMouseFeeder
  {
    void Connect();
    void Feed(MouseReport mouseReport);
  }
}
