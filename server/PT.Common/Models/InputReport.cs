using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public class InputReport
  {
    public ulong Buttons { get; set; }
    public Axis Axis1 { get; set; }
    public Axis Axis2 { get; set; }
    public int Throttle { get; set; }
    public int Brake { get; set; }
  }

  public class Axis
  {
    public int X { get; set; }
    public int Y { get; set; }
    public int Z { get; set; }
  }
}
