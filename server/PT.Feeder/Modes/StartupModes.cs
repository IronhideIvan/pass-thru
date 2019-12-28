namespace PT.Feeder
{
  internal enum StartupModes
  {
    UDP,
    Sample
  }

  public interface IModeRunner
  {
    void Run(string[] args);
  }
}
