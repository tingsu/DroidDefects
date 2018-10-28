package caldwell.ben.bites;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

/** Listen for received sms and interpret raw pdus into messages to check for recipes.
 * Creates a recipe received notification with an intent to add the new recipe to the
 * Bites content provider. 
 * 
 * @author Ben Caldwell
 *
 */
public class SmsReceiver extends BroadcastReceiver {
	
	public static final String KEY_RECIPE = "recipeName";
	public static final String KEY_ING_ARRAY = "ingredientArray";
	public static final String KEY_METH_ARRAY = "methodArray";
	public static final String KEY_METH_STEP_ARRAY = "methodStepArray";
	public static final String KEY_NOTIFY_ID = "notifyId";
	
	private static int ID = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String message = "asdf";
		int start, end;
		
		if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			return;
		}
		
		Bundle bdl = intent.getExtras();
		
		Object pdus[] = (Object[])bdl.get("pdus");
		for (int n=0; n<pdus.length; n++) {
			byte byteData[] = (byte[])pdus[n];
			message = SmsMessage.createFromPdu(byteData).getDisplayMessageBody();
		}
		
		if (message.contains("***Bites Recipe***")) {
	
			Intent broadcast = new Intent("caldwell.ben.bites.RECEIVED_RECIPE");
			broadcast.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			/**
			 * Get the recipe name from between ***Bites Recipe** and **Ingredients**
			 */
			start = message.indexOf("***Bites Recipe***");
			start = message.indexOf("\n", start) + 1;
			end = message.indexOf("**Ingredients**") - 1;
			String recipe = message.substring(start, end);
			//Recipe name
			broadcast.putExtra(KEY_RECIPE, recipe);
			
			/**
			 * Create an ingredients string array by splitting the message 
			 * between **Ingredients** and **Method** at newline characters
			 */
			start = message.indexOf("**Ingredients**");
			start = message.indexOf("\n", start) + 1;
			end = message.indexOf("**Method**");
			String ingredients[] = message.substring(start, end).split("\n");
			//Load the ingredients array into the intent extras
			broadcast.putExtra(KEY_ING_ARRAY, ingredients);

			/**
			 * Create a methods string array by splitting the message 
			 * between **Ingredients** and **Method** at newline characters
			 */
			start = message.indexOf("**Method**");
			start = message.indexOf("\n", start) + 1;
			end = message.indexOf("***",start);
			String methods[] = message.substring(start, end).split("\n");
			
			int methodSteps[] = new int[methods.length];
			//split method steps to get step number and string
			for (int i=0; i<methods.length; i++)
			{
				start = methods[i].indexOf(".");
				methodSteps[i] = Integer.parseInt(methods[i].substring(0, start));
				methods[i] = methods[i].substring(start + 1) ;
			}
			
			//load the methods array into the intent extras
			broadcast.putExtra(KEY_METH_ARRAY, methods);
			
			//Method steps array
			broadcast.putExtra(KEY_METH_STEP_ARRAY, new int[]{1,2});

			/**
			 * Notification ID
			 * Increment the notificaton id and use this as a UID for the notication
			 * This ID will be needed to cancel the notification so pass it as an extra 
			 */
			ID += 1;
			broadcast.putExtra(KEY_NOTIFY_ID, ID);
	
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

			PendingIntent pendIntent = PendingIntent.getActivity(
															context, 
															0, 
															broadcast, 
															Intent.FLAG_ACTIVITY_NEW_TASK);
			Notification notification = new Notification(R.drawable.icon, "Recipe Received",System.currentTimeMillis());
			notification.setLatestEventInfo(context, "Recipe Received", recipe, pendIntent);
			nm.notify(ID, notification);

		}				
	}
}
