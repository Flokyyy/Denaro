package de.flokyy.denaro.listener;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.flokyy.denaro.NormieWalletAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class EventListener extends ListenerAdapter {
	
	
		@Override
	    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getInteraction() == null) {
			return;
		}
		
		if (event.getName().equalsIgnoreCase("create")) {
        	ExecutorService executorService = Executors.newSingleThreadExecutor();			
        	executorService.execute(new Runnable() {
			  public void run() {
			 
				event.deferReply(true).queue(); // Let the user know we received the command before doing anything else
				InteractionHook hook = event.getHook(); // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
				hook.setEphemeral(true);
					
				OptionMapping pin = event.getOption("pin");
				Integer pinPassword = pin.getAsInt();
				 
				if(NormieWalletAdapter.checkIfWalletExists(event.getUser().getId())) {
			    	hook.sendMessage("You already have a Denaro wallet. Use /wallet to check your Denaro wallet.").setEphemeral(true).queue();
			    	return;
				}
				
				if(!NormieWalletAdapter.checkIfWalletExists(event.getUser().getId())) {
					String wallet = NormieWalletAdapter.getUserWallet("" + pinPassword, event.getUser().getId());
					EmbedBuilder help31 = new EmbedBuilder();
				    help31.setTitle("WALLET CREATED");
					help31.setDescription(":white_check_mark: Great news! Your new Denaro wallet was just created!");
				
					help31.addField(":warning: Reminder", "Your secret pin is: ``" + pinPassword +"`` Make sure to remember this pin, otherwise your funds will be lost.", false);
					help31.addField("Publickey", "``" + wallet + "``", false);
					help31.setColor(Color.green);
			
					help31.setFooter("Powered by Denaro", "https://cdn.discordapp.com/attachments/1048663249488453693/1082423467472867439/Neues_Projekt_2.png");
					
					hook.sendMessageEmbeds(help31.build()).queue();
					help31.clear();	
					return;
			    	
				}
			  }
			});
			executorService.shutdown();
         }
	
		 if (event.getName().equalsIgnoreCase("wallet")) {
		      
	        	ExecutorService executorService = Executors.newSingleThreadExecutor();			
	        	executorService.execute(new Runnable() {
				  public void run() {
				 
					event.deferReply(true).queue(); // Let the user know we received the command before doing anything else
					InteractionHook hook = event.getHook(); // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
					hook.setEphemeral(true);
						
					if(!NormieWalletAdapter.checkIfWalletExists(event.getUser().getId())) {
						hook.sendMessage("You don't have any wallet yet! Please use /create first.").queue();
						return;
					}
					
					if(NormieWalletAdapter.checkIfWalletExists(event.getUser().getId())) {
						String wallet = NormieWalletAdapter.getWalletWithoutPIN(event.getUser().getId());
				    	hook.sendMessage("Your Denaro wallet is: " + wallet).setEphemeral(true).queue();
					}
				  }
				});
				executorService.shutdown();
	        }
	    
		 if (event.getName().equalsIgnoreCase("tip")) {
	  	      
	        	ExecutorService executorService = Executors.newSingleThreadExecutor();
				executorService.execute(new Runnable() {
				public void run() {
				
					event.deferReply(true).queue(); // Let the user know we received the command before doing anything else
					InteractionHook hook = event.getHook(); // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
					hook.setEphemeral(true);
				 
				
				    OptionMapping receiver = event.getOption("receiver");
			        OptionMapping amountS = event.getOption("amount");
			        
			        OptionMapping pin = event.getOption("pin");
					Integer pinPassword = pin.getAsInt();
			          
			        String s = amountS.getAsString();
			         
			        User receiverUser = receiver.getAsUser();

					
					double convertedAmount;
					try
					{
					  convertedAmount = Double.parseDouble(s);
					  
					}
					catch(NumberFormatException e)
					{
						hook.sendMessage("Your amount is invaild! Example amount: ``0.01``").queue();
						return;
					}
					
					if(convertedAmount == 0.0 || convertedAmount == 0 || convertedAmount < 0.0 || convertedAmount < 0) {
						hook.sendMessage("Your amount is invaild! Example amount: ``0.01``").queue();
						return;
					}
					
					String amountString = "" + convertedAmount;

					if(!NormieWalletAdapter.checkIfWalletExists(event.getUser().getId())) {
						hook.sendMessage("You don't have any wallet yet! Please use /create first.").queue();
						return;
					}
					
					if(!NormieWalletAdapter.checkIfWalletExists(receiverUser.getId())) {
						hook.sendMessage("This user has no Denaro wallet yet.").queue();
						return;
					}
					
					if(NormieWalletAdapter.checkIfWalletExists(receiverUser.getId())) {
						
						String wallet = NormieWalletAdapter.getUserWallet("" + pinPassword, receiverUser.getId());
						System.out.println(wallet);
						
						String signature = NormieWalletAdapter.sendTransaction("" + pinPassword, event.getUser().getId(), wallet, amountString);
						
						if(!signature.equalsIgnoreCase("ERROR")) {
							EmbedBuilder help31 = new EmbedBuilder();
						    help31.setTitle("TRANSACTION SENT");
							help31.setDescription(":white_check_mark: Your transaction is pending now, but should be confirmed soon.");
						
							help31.setColor(Color.green);
							help31.setFooter("Powered by Denaro", "https://cdn.discordapp.com/attachments/1048663249488453693/1082423467472867439/Neues_Projekt_2.png");
							
							hook.sendMessageEmbeds(help31.build()).addActionRow(Button.link("https://solscan.io/tx/" + signature, "Transaction")).queue();
							help31.clear();	
							return;
						}
						else {
							
							hook.sendMessage("Your transaction failed. Please try again!").queue();
							return;
							}		  
						}
				}
				});
				executorService.shutdown();

	        }
	             
	        if (event.getName().equalsIgnoreCase("withdraw")) {
	  	      
	        	ExecutorService executorService = Executors.newSingleThreadExecutor();
				executorService.execute(new Runnable() {
				public void run() {
				
					event.deferReply(true).queue(); // Let the user know we received the command before doing anything else
					InteractionHook hook = event.getHook(); // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
					hook.setEphemeral(true);
				 
				
				    OptionMapping wallet = event.getOption("wallet");
			        OptionMapping amountS = event.getOption("amount");
			        OptionMapping pin = event.getOption("pin");
					Integer pinPassword = pin.getAsInt();
			          
			        String s = amountS.getAsString();
			         
			        String receiver = wallet.getAsString();

					
					double convertedAmount;
					try
					{
					  convertedAmount = Double.parseDouble(s);
					  
					}
					catch(NumberFormatException e)
					{
						hook.sendMessage("Your amount is invaild! Example amount: ``0.01``").queue();
						return;
					}
					
					if(convertedAmount == 0.0 || convertedAmount == 0 || convertedAmount < 0.0 || convertedAmount < 0) {
						hook.sendMessage("Your amount is invaild! Example amount: ``0.01``").queue();
						return;
					}
					
					String amountString = "" + convertedAmount;
					String signature = NormieWalletAdapter.sendTransaction("" + pinPassword, event.getUser().getId(), receiver, amountString);
					if(!signature.equalsIgnoreCase("ERROR")) {
						EmbedBuilder help31 = new EmbedBuilder();
					    help31.setTitle("TRANSACTION SENT");
						help31.setDescription(":white_check_mark: Your transaction is pending now, but should be confirmed soon.");
					
						help31.setColor(Color.green);
				
						help31.setFooter("Powered by Denaro", "https://cdn.discordapp.com/attachments/1048663249488453693/1082423467472867439/Neues_Projekt_2.png");
						
						hook.sendMessageEmbeds(help31.build()).addActionRow(Button.link("https://solscan.io/tx/" + signature, "Transaction")).queue();
						help31.clear();	
						return;
					}
					else {
						hook.sendMessage("Your transaction failed. Please try again!").queue();
						return;
					}		  
				}
				});
				executorService.shutdown();

	        }
		}
}
