// @GENERATOR:play-routes-compiler
// @SOURCE:/mnt/d/Documents/PI/play-java-starter-example/conf/routes
// @DATE:Tue Nov 27 18:42:40 GMT 2018


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