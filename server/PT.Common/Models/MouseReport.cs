using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public class MouseReport
  {
    public long MessageTimestamp { get; set; }
    public ButtonReport Buttons { get; set; }
    public Axis Velocity { get; set; }
  }
}
