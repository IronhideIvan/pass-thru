using System;
using System.Collections.Generic;
using System.Text;

namespace PT.Common
{
  public class ServiceProvider
  {
    private static ServiceProvider _instance;
    public static ServiceProvider Instance
    {
      get
      {
        if (_instance == null)
        {
          _instance = new ServiceProvider();
        }
        return _instance;
      }
    }

    // Purely syntactic sugar so that ServiceProvider.Instance.GetService<T>() didn't need to be written everywhere.
    public static T Get<T>()
      where T : class
    {
      return Instance.GetService<T>();
    }

    private Dictionary<string, object> _singletons = new Dictionary<string, object>();
    private Dictionary<string, Func<object>> _transients = new Dictionary<string, Func<object>>();

    public void RegisterSingleton<TInterface, TImplementation>()
      // Godot does not like that there are two 'where' clauses here
      // where TInterface : class
      where TImplementation : class, TInterface, new()
    {
      string interfaceName = typeof(TInterface).Name;
      if (_singletons.ContainsKey(interfaceName))
      {
        throw new PTGenericException($"Unable to register the singleton service of type '{interfaceName}'. This service is already registered in this provider.");
      }
      _singletons.Add(interfaceName, new TImplementation());
    }

    public void RegisterTransient<TInterface, TImplementation>()
      // where TInterface : class
      where TImplementation : class, TInterface, new()
    {
      string interfaceName = typeof(TInterface).Name;
      if (_transients.ContainsKey(interfaceName))
      {
        throw new PTGenericException($"Unable to register the transient service of type '{interfaceName}'. This service is already registered in this provider.");
      }
      _transients.Add(interfaceName, () => new TImplementation());
    }

    public T GetService<T>()
      where T : class
    {
      string TName = typeof(T).Name;
      if (_singletons.ContainsKey(TName))
      {
        return (T)_singletons[TName];
      }

      if (_transients.ContainsKey(TName))
      {
        var impl = _transients[TName]();
        return (T)impl;
      }

      throw new PTGenericException($"No service found of type '{TName}'");
    }
  }
}
