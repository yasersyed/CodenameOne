package com.codename1.samples;


import com.codename1.components.SpanLabel;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import java.io.IOException;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.NetworkEvent;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.CommonProgressAnimations;
import com.codename1.ui.CommonProgressAnimations.LoadingTextAnimation;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.util.AsyncResource;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class LoadingTextAnimationSample {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
     private void showForm() {
        Form f = new Form("Hello", new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        Form prev = CN.getCurrentForm();
        Toolbar tb = new Toolbar();
        f.setToolbar(tb);
        tb.addCommandToLeftBar("Back", null, evt->{
            prev.showBack();
        });
        SpanLabel profileText = new SpanLabel();
        
        profileText.setText("placeholder");
        f.add(BorderLayout.CENTER, profileText);
        // Replace the label by a CircleProgress to indicate that it is loading.
        LoadingTextAnimation.markComponentLoading(profileText);
        Button next = new Button("Next");
        next.addActionListener(e->{
            showLabelTest();
        });
        f.add(BorderLayout.SOUTH, next);
        AsyncResource<MyData> request = fetchDataAsync();
        request.ready(data -> {
            profileText.setText(data.getProfileText());

            // Replace the progress with the nameLabel now that
            // it is ready, using a fade transition
            LoadingTextAnimation.markComponentReady(profileText, CommonTransitions.createFade(300));
        });
        
        f.show();

    }
     
     
     private void showLabelTest() {
         Form f = new Form("Hello", BoxLayout.y());
         Label l = new Label("placeholder");
         f.add(l);
         LoadingTextAnimation.markComponentLoading(l);
         f.show();
         Timer t = new Timer();
         t.schedule(new TimerTask() {
             public void run(){
                CN.callSerially(()->{
                    LoadingTextAnimation.markComponentReady(l);
                });
             }
         }, 2000);
     }
     
     private class MyData {
        String getProfileText() {
            return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        }
    }
    
    private AsyncResource<MyData> fetchDataAsync() {
        final AsyncResource<MyData> out = new AsyncResource<>();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            
            @Override
            public void run() {
                out.complete(new MyData());
            }
            
        }, 2000);
        
        return out;
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        Form hi = new Form("Hi World", BoxLayout.y());
        Button b = new Button("Show Details");
        b.addActionListener(e->{
            showForm();
        });
        hi.add(b);
        hi.show();
    }

    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }

}
