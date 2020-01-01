
using PT.Common;

namespace PT.MouseFeeder
{
  public class Feeder : IMouseFeeder
  {
    public void Feed(MouseReport mouseReport)
    {
      Win32.SetCursorPos(10, 10);
    }
  }
}