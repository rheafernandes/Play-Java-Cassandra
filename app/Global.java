import javafx.application.Application;
import play.GlobalSettings;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.lang.reflect.Method;

public class Global extends GlobalSettings {


    public void onStart(Application app) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");

    }

    @SuppressWarnings("rawtypes")
    public Action onRequest(Http.Request request, Method actionMethod) {
        long startTime = System.currentTimeMillis();
        return new Action.Simple() {
            public F.Promise<Result> call(Http.Context ctx) throws Throwable {
                F.Promise<Result> call = delegate.call(ctx);
                call.onRedeem((r) -> {

                });
                return call;
            }
        };
    }
}