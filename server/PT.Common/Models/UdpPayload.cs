using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public enum PayloadStatus
  {
    None,
    Success,
    Failure
  }

  public enum PayloadType
  {
    Controller,
    Heartbeat,
    Mouse,
    Authentication
  }

  public class UdpPayload
  {
    public PayloadStatus Status { get; set; }
    public PayloadType Type { get; set; }
    public string Payload { get; set; }

    public static UdpPayload Parse(string strPayload)
    {
      var payload = new UdpPayload
      {
        Status = PayloadStatus.Failure
      };

      if (string.IsNullOrWhiteSpace(strPayload))
      {
        return payload;
      }

      char payloadType = strPayload[0];
      switch (payloadType)
      {
        // Controller
        case 'C':
          payload.Type = PayloadType.Controller;
          if (strPayload.Length > 1)
          {
            payload.Payload = strPayload.Substring(1);
          }
          else
          {
            return payload;
          }
          break;

        // Mouse
        case 'M':

        // Heartbeat
        case 'H':

        // Authenticate
        case 'A':
        default:
          return payload;
      }

      payload.Status = PayloadStatus.Success;
      return payload;
    }
  }
}
