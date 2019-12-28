﻿using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace PT.Common
{
  public interface IUdpSocket
  {
    void InitializeServer(string address, int port, Action<string> callback);
    void InitializeClient(string address, int port, Action<string> callback);
    void Send(string payload);
  }

  // This was initially set up thanks to https://gist.github.com/darkguy2008/413a6fea3a5b4e67e5e0d96f750088a9
  public class UdpSocket : IUdpSocket
  {
    private static readonly Lazy<IAppLogger> _lazyLogger = new Lazy<IAppLogger>(() => ServiceProvider.Get<IAppLogger>().Configure(typeof(UdpSocket)));
    private static readonly IAppLogger _logger = _lazyLogger.Value;
    private Socket _socket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
    private const int bufSize = 8 * 1024;
    private State state = new State();
    private EndPoint epFrom = new IPEndPoint(IPAddress.Any, 0);
    private AsyncCallback recv = null;

    public class State
    {
      public byte[] buffer = new byte[bufSize];
    }

    public void InitializeServer(string address, int port, Action<string> callback)
    {
      _socket.SetSocketOption(SocketOptionLevel.IP, SocketOptionName.ReuseAddress, true);
      _socket.Bind(new IPEndPoint(IPAddress.Parse(address), port));
      Receive(callback);
    }

    public void InitializeClient(string address, int port, Action<string> callback)
    {
      _socket.Connect(IPAddress.Parse(address), port);
      Receive(callback);
    }

    public void Send(string payload)
    {
      byte[] data = Encoding.ASCII.GetBytes(payload);
      _socket.BeginSend(data, 0, data.Length, SocketFlags.None, (ar) =>
      {
        State so = (State)ar.AsyncState;
        int bytes = _socket.EndSend(ar);
        if (LogLevel.Debug <= _logger.LogLevel)
        {
          _logger.Debug($"SEND: {bytes}, {payload}");
        }
      }, state);
    }

    private void Receive(Action<string> callback)
    {
      _socket.BeginReceiveFrom(state.buffer, 0, bufSize, SocketFlags.None, ref epFrom, recv = (ar) =>
      {
        State so = (State)ar.AsyncState;
        int bytes = _socket.EndReceiveFrom(ar, ref epFrom);
        _socket.BeginReceiveFrom(so.buffer, 0, bufSize, SocketFlags.None, ref epFrom, recv, so);

        string payload = Encoding.ASCII.GetString(so.buffer, 0, bytes);
        if (LogLevel.Debug <= _logger.LogLevel)
        {
          _logger.Debug($"RECV: {epFrom.ToString()}: {bytes}, {payload}");
        }
        if (callback != null)
        {
          callback(payload);
        }
      }, state);
    }
  }
}
