package servlets

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

/*import autocomplete.{SimpleWordMatcher, PrefixMatcher}*/
import scala.collection.JavaConversions._

/**
  * Created by lovesqcc on 16-3-19.
  */
class AutoCompleteServletUsingScala extends HttpServlet {

/*  protected var wordMatcher: PrefixMatcher = new SimpleWordMatcher*/
  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    doPost(req, resp)
  }

  @throws(classOf[ServletException])
  @throws(classOf[IOException])
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.setContentType("text/plain;charset=UTF8")
    val inputText: String = req.getParameter("inputText")
/*    val matchers = wordMatcher.obtainMatchedWords(inputText)*/

    var content = ""
/*    matchers.foreach {
      word => content += word + " "
    }*/

    println("hello");
    resp.getWriter print content

  }

}
