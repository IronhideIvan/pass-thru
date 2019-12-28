using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using PT.Common;
using Newtonsoft.Json;

namespace PT.WebApi.Controllers
{
  [Route("api/[controller]")]
  [ApiController]
  public class FeederController : ControllerBase
  {
    public FeederController()
    {
    }

    // GET api/values
    [HttpGet("{buttonId}")]
    public ActionResult<string> Get(int buttonId)
    {
      try
      {
        var udpSocket = ServiceProvider.Get<IUdpSocket>();
        udpSocket.InitializeClient("127.0.0.1", 7084, null);
        udpSocket.Send(JsonConvert.SerializeObject(new InputReport { Buttons = (ulong)buttonId }));
      }
      catch (Exception ex)
      {
        return BadRequest(ex.Message);
      }
      return Ok("Success");
    }
  }
}
