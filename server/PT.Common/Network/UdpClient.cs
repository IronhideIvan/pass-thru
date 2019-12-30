using System;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace PT.Common
{
  public class PtUdpClient : IUdpSocket
  {
    private static readonly Lazy<IAppLogger> _lazyLogger = new Lazy<IAppLogger>(() => ServiceProvider.Get<IAppLogger>().Configure(typeof(PtUdpClient)));
    private static readonly IAppLogger _logger = _lazyLogger.Value;

    private Task _backgroundWorker;
    private UdpClient _client;
    private IPEndPoint _remoteIpEndPoint = new IPEndPoint(IPAddress.Any, 0);

    public void InitializeClient(string address, int port, Action<string> callback)
    {
      throw new NotImplementedException();
    }

    public void InitializeServer(string address, int port, Action<string> callback)
    {
      _client = new UdpClient(address, port);
      if (_backgroundWorker == null)
      {
        _backgroundWorker = Task.Factory.StartNew(() =>
        {
          while (true)
          {
            try
            {
              var data = _client.Receive(ref _remoteIpEndPoint);
              var message = Encoding.ASCII.GetString(data);
              _logger.Debug(message);
              callback(message);
            }
            catch (Exception ex)
            {
              _logger.Error(ex.Message);
            }

            Thread.Sleep(10);
          }
        });
      }
    }

    public void Send(string payload)
    {
      throw new NotImplementedException();
    }

    #region IDisposable Support
    private bool disposedValue = false; // To detect redundant calls

    protected virtual void Dispose(bool disposing)
    {
      if (!disposedValue)
      {
        if (disposing)
        {
          // TODO: dispose managed state (managed objects).
        }

        // TODO: free unmanaged resources (unmanaged objects) and override a finalizer below.
        // TODO: set large fields to null.

        disposedValue = true;
      }
    }

    // TODO: override a finalizer only if Dispose(bool disposing) above has code to free unmanaged resources.
    // ~PtUdpClient()
    // {
    //   // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
    //   Dispose(false);
    // }

    // This code added to correctly implement the disposable pattern.
    public void Dispose()
    {
      // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
      Dispose(true);
      // TODO: uncomment the following line if the finalizer is overridden above.
      // GC.SuppressFinalize(this);
    }
    #endregion
  }
}
