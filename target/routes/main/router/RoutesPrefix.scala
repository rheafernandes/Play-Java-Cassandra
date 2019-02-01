
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/sanjay/Desktop/Java/Ekstep/onboard-play/conf/routes
// @DATE:Fri Feb 01 14:20:29 IST 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
