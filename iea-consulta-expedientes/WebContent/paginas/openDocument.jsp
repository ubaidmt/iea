<%@page import="java.util.*,java.io.*" errorPage=""%>
<%@page import="mx.com.dss.inap.services.ContentEngineServices" %>
<%@page contentType="text/html; charset=utf-8"	pageEncoding="UTF-8"%>

<%

  String user;
  String pathContext = request.getParameter("pathContext") == null ? "" : request.getParameter("pathContext");
  String FileName = request.getParameter("FileName") == null ? "" : request.getParameter("FileName");
  String Id = request.getParameter("Id") == null ? "" : request.getParameter("Id");
  
  ContentEngineServices ceService = new ContentEngineServices(pathContext);
  Random random = new Random();
  
  try {

    String pathScr = getServletContext().getRealPath("scratch");
    String extension = ".pdf";
    
    if (FileName.lastIndexOf(".jpg") != -1)
    	extension = ".jpg";
    	
    String ruta = pathScr + "/" + FileName + random.nextLong() + extension;

    if (ceService.isConected()) {
      ruta = ceService.getContent(Id, ruta);
    }else{
        System.out.println("Se perdio la conexion al servidor");
    }

    File myfile = new File(ruta);
    FileInputStream fis = new FileInputStream(myfile);
    int fisTam = fis.available();
    byte [] datos = new byte[fisTam];

    fis.read(datos, 0, fisTam);

    String ContentType = "application/pdf";
    if (ruta.lastIndexOf(".jpg") != -1)
      ContentType = "image/jpg";

    response.setContentType(ContentType);
    response.addHeader("Content-Disposition", "attachment; filename=" + myfile.getName());
    response.setContentLength(fisTam);
    response.getOutputStream().write(datos);
    response.getOutputStream().flush();
    fis.close();
    myfile = null;

  }catch (Exception ex) {
    ex.printStackTrace();
    
  }finally {
  	ceService.doDisconnect();
  }
%>
