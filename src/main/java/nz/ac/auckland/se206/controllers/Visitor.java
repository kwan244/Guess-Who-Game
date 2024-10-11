package nz.ac.auckland.se206.controllers;

/** Represents a visitor in the system with their details. */
public class Visitor {

  private int id;
  private String name;
  private String checkinTime;
  private String checkoutTime;
  private String host;

  /**
   * Constructs a Visitor instance with the specified details.
   *
   * @param id the unique identifier for the visitor
   * @param name the name of the visitor
   * @param checkinTime the time the visitor checked in
   * @param checkoutTime the time the visitor checked out
   * @param host the name of the host the visitor is visiting
   */
  public Visitor(int id, String name, String checkinTime, String checkoutTime, String host) {
    this.id = id;
    this.name = name;
    this.checkinTime = checkinTime;
    this.checkoutTime = checkoutTime;
    this.host = host;
  }

  /**
   * Gets the unique identifier of the visitor.
   *
   * @return the visitor's ID
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the name of the visitor.
   *
   * @return the visitor's name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the check-in time of the visitor.
   *
   * @return the check-in time
   */
  public String getCheckinTime() {
    return checkinTime;
  }

  /**
   * Gets the check-out time of the visitor.
   *
   * @return the check-out time
   */
  public String getCheckoutTime() {
    return checkoutTime;
  }

  /**
   * Gets the host of the visitor.
   *
   * @return the name of the host
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets the unique identifier for the visitor.
   *
   * @param id the new visitor ID
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Sets the name of the visitor.
   *
   * @param name the new visitor name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the check-in time of the visitor.
   *
   * @param checkinTime the new check-in time
   */
  public void setCheckinTime(String checkinTime) {
    this.checkinTime = checkinTime;
  }

  /**
   * Sets the check-out time of the visitor.
   *
   * @param checkoutTime the new check-out time
   */
  public void setCheckoutTime(String checkoutTime) {
    this.checkoutTime = checkoutTime;
  }

  /**
   * Sets the host of the visitor.
   *
   * @param host the new host name
   */
  public void setHost(String host) {
    this.host = host;
  }
}
