package cc.voox.test;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class ConnTest {
    final static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        String s = "http://192.168.126.88/GetCurrencyList.aspx?routeDB=Montreal";
//        for (int i = 0; i < 10000; i++) {
//            System.out.println("execute index: " + i + " " + LocalDateTime.now().toString());
//            testOkhttpSync(s, i);
//            testOkhttpAsync(s, i);
//        }
        for (int i = 0; i < 2000; i++) {
            final int j = i;
            Runnable t2 = () -> {
                for (int k = 0; k < 10000; k++) {
                    testOkhttpSync(s + "&xx="+k, k);
                }
            };
            new Thread( t2, "thread: " + j).start();
        }
//        testOkhttpSync(s, 1);
//        testOkhttpSync(s, 2);
        //hangs...
//        testHttpConn(s);


    }

    public static void testHttpConn(String urlStr) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb);
            conn.disconnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static void testOkhttpAsync(String url, int i) {


        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("index:" + i + " >> " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("index: " + i + "  >> " + LocalDateTime.now().toString() + ">>> " + Objects.requireNonNull(response.body()).string());
            }
        });

    }

    static void testOkhttpSync(String url, int i) {


        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.print("Thread:"+ Thread.currentThread().getName()+"  index: " + i + "  >> " + LocalDateTime.now().toString() + ">>> " + response.body().string());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
