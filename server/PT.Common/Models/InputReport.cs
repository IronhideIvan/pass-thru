using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public class InputReport
  {
    public long MessageTimestamp { get; set; }
    public ButtonReport ButtonReport { get; set; }
    public InputAxisReport AxisReport { get; set; }
  }

  public class InputAxisReport
  {
    public Axis Axis1 { get; set; }
    public Axis Axis2 { get; set; }
    public float Throttle { get; set; }
    public float Brake { get; set; }
  }
}
