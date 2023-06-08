interface IMail {
  public boolean ok();

  public boolean send(String to, String subject, String body);
}
