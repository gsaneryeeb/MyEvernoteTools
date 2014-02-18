package com.saneryee.evernote;

/*
  Evernote API sample code, structured as a simple command line
  application that demonstrates several API calls.
  
  To compile (Unix):
    javac -classpath ../../target/evernote-api-*.jar EDAMDemo.java
 
  To run:
    java -classpath ../../target/evernote-api-*.jar EDAMDemo
 
  Full documentation of the Evernote API can be found at 
  http://dev.evernote.com/documentation/cloud/
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.transport.TTransportException;

public class MyEvernoteTools {

  /***************************************************************************
   * You must change the following values before running this sample code *
   ***************************************************************************/

  // Real applications authenticate with Evernote using OAuth, but for the
  // purpose of exploring the API, you can get a developer token that allows
  // you to access your own Evernote account. To get a developer token, visit
  // https://sandbox.evernote.com/api/DeveloperToken.action
  private static final String AUTH_TOKEN = "your developer token";

  /***************************************************************************
   * You shouldn't need to change anything below here to run sample code *
   ***************************************************************************/

  private UserStoreClient userStore;
  private NoteStoreClient noteStore;
  

  /**
   * Console entry point.
   */
  public static void main(String args[]) throws Exception {
    String token = System.getenv("AUTH_TOKEN");
    if (token == null) {
      token = AUTH_TOKEN;
    }
    if ("your developer token".equals(token)) {
      System.err.println("Please fill in your developer token");
      System.err
          .println("To get a developer token, go to https://sandbox.evernote.com/api/DeveloperToken.action");
      return;
    }

    MyEvernoteTools demo = new MyEvernoteTools(token);
    try {
      demo.AutoManageNotes();
     
    } catch (EDAMUserException e) {
      // These are the most common error types that you'll need to
      // handle
      // EDAMUserException is thrown when an API call fails because a
      // paramter was invalid.
      if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
        System.err.println("Your authentication token is expired!");
      } else if (e.getErrorCode() == EDAMErrorCode.INVALID_AUTH) {
        System.err.println("Your authentication token is invalid!");
      } else if (e.getErrorCode() == EDAMErrorCode.QUOTA_REACHED) {
        System.err.println("Your authentication token is invalid!");
      } else {
        System.err.println("Error: " + e.getErrorCode().toString()
            + " parameter: " + e.getParameter());
      }
    } catch (EDAMSystemException e) {
      System.err.println("System error: " + e.getErrorCode().toString());
    } catch (TTransportException t) {
      System.err.println("Networking error: " + t.getMessage());
    }
  }

  /**
   * Intialize UserStore and NoteStore clients. During this step, we
   * authenticate with the Evernote web service. All of this code is boilerplate
   * - you can copy it straight into your application.
   */
  public MyEvernoteTools(String token) throws Exception {
    // Set up the UserStore client and check that we can speak to the server
    EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
    ClientFactory factory = new ClientFactory(evernoteAuth);
    userStore = factory.createUserStoreClient();

    boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
        com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
        com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
    if (!versionOk) {
      System.err.println("Incompatible Evernote client protocol version");
      System.exit(1);
    }

    // Set up the NoteStore client
    noteStore = factory.createNoteStoreClient();
  }

  /**
   * Retrieve and display a list of the user's notes.
   */
  private void AutoManageNotes() throws Exception {
    // List the notes in the user's account
    System.out.println("Listing notes:");

    // First, get a list of all notebooks
    List<Notebook> notebooks = noteStore.listNotebooks();

    for (Notebook notebook : notebooks) {
      System.out.println("Notebook: " + notebook.getName());

      // Next, search for the first 100 notes in this notebook, ordering
      // by creation date
      NoteFilter filter = new NoteFilter();
      filter.setNotebookGuid(notebook.getGuid());
      filter.setOrder(NoteSortOrder.CREATED.getValue());
      filter.setAscending(true);

      NoteList noteList = noteStore.findNotes(filter, 0, 100);
      List<Note> notes = noteList.getNotes();
      
      for (Note note : notes) {
    	String noteTitle = note.getTitle();
    	
    	/**
    	 * 历史任务判断
    	long lastDate =1392198202000L ;
    	
    	if (note.getUpdated()>lastDate){
    		
    		//执行操作
    		System.out.println(" *  new noteTitle = " + noteTitle);
    		
    	}
    	**/
    	int flagMyYinxiang = 0;
    	flagMyYinxiang = noteTitle.indexOf("@我的印象笔记");
    	
    	/**
                           添加Tag
        */
    	if (flagMyYinxiang>=0){
	
    		//匹配
            Pattern p = Pattern.compile("\\#(.*?)\\#");//正则表达式，取=和|之间的字符串，不包括=和|
    		Matcher m = p.matcher(noteTitle);
    		while(m.find()) {
 
    			//System.out.println(" ** New Tag is: "+m.group(1));//m.group(1)不包括这两个字符
    			//添加标签
    			Note noteNew = noteStore.getNote(note.getGuid(), true, true, false, false);

    		    // Do something with the note contents or resources...

    		    // Now, update the note. Because we're not changing them, we unset
    		    // the content and resources. All we want to change is the tags.
    			noteNew.unsetContent();
    			noteNew.unsetResources();

    		    // We want to apply the tag "TestTag"
    			noteNew.addToTagNames(m.group(1));

    		    // Now update the note. Because we haven't set the content or resources,
    		    // they won't be changed.
    		    noteStore.updateNote(noteNew);
    		    
    		    System.out.println("Successfully added tag to existing noteNew");

            }	
    	}
      }
    }
    System.out.println();
  }
}
