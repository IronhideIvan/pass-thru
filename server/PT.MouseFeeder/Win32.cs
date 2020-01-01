using System;
using System.Runtime.InteropServices;

namespace PT.MouseFeeder
{
  internal class Win32
  {
    [DllImport("User32.Dll")]
    public static extern bool GetCursorPos(out POINT lpPoint);

    [DllImport("User32.Dll")]
    public static extern bool SetCursorPos(int x, int y);

    [DllImport("User32.Dll")]
    public static extern bool ClientToScreen(IntPtr hWnd, ref POINT point);

    // The the window that has keyboard focus
    [DllImport("User32.Dll")]
    public static extern IntPtr MonitorFromPoint(POINT pt, uint dwFlags);

    [DllImport("User32.Dll")]
    public static extern bool GetMonitorInfo(IntPtr hMonitor, [In, Out] MONITORINFOEX lpmi);

    [DllImport("user32.dll", CharSet = CharSet.Auto, CallingConvention = CallingConvention.StdCall)]
    public static extern void mouse_event(uint dwFlags, uint dx, uint dy, uint cButtons, uint dwExtraInfo);

    internal class DWORD_FLAGS
    {
      public const uint MONITOR_DEFAULTTONULL = 0;
      public const uint MONITOR_DEFAULTTOPRIMARY = 1;
      public const uint MONITOR_DEFAULTTONEAREST = 2;
      public const int MOUSEEVENTF_LEFTDOWN = 2;
      public const int MOUSEEVENTF_LEFTUP = 4;
      public const int MOUSEEVENTF_RIGHTDOWN = 8;
      public const int MOUSEEVENTF_RIGHTUP = 16;
    }

    [StructLayout(LayoutKind.Sequential)]
    internal struct POINT
    {
      public int x;
      public int y;

      public POINT(int x, int y)
      {
        this.x = x;
        this.y = y;
      }
    }

    [StructLayout(LayoutKind.Sequential)]
    internal struct RECT
    {
      public int left;
      public int top;
      public int right;
      public int bottom;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto, Pack = 4)]
    public class MONITORINFOEX
    {
      public int cbSize = 72;
      public RECT rcMonitor = new RECT();
      public RECT rcWork = new RECT();
      public int dwFlags = 0;

      [MarshalAs(UnmanagedType.ByValArray, SizeConst = 32)]
      public char[] szDevice = new char[32];
    }
  }
}
