module.exports = function (app) {
    app.use(function (req, res, next) {
      res.setHeader('Content-Security-Policy', 
        "img-src 'self' http://localhost:8080 blob:;" +                     //1.
        "script-src 'self';" +                                              //2.
        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com;" +  //3.
        "object-src 'none';" +                                              //4.
        "base-uri 'self';" +                                                //5.
        "font-src 'self' https://fonts.gstatic.com;" +                      //6.
        "connect-src 'self' http://localhost:8080;" +                       //7.
        "default-src 'self';"                                               //8.
      );
      next();
    });
  };