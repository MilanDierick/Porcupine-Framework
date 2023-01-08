/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import init.sprite.UI.UI;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GButt;
import util.gui.misc.GText;
import view.main.Message;

@SuppressWarnings({"HardcodedLineSeparator", "SerializableDeserializableClassInSecureContext", "SerializableHasSerializationMethods"})
public class RaidersMessage extends Message {
	public static final CharSequence MESSAGE_BODY = "Greetings, Chief of %s. I am the leader of a raider group, and we have come upon your settlement filled with riches that we desire.\n" + "\n" + "We offer you an ultimatum: surrender your resources to us, and we will try not to kill too many of your people. However, if you refuse to comply, we will have no choice but to raid your settlement, killing and taking what we desire.\n" + "\n" + "We understand that this is not an easy decision for you, but we assure you that it is in the best interest of your people to surrender to us. Think carefully about your options, as the consequences of your choice will greatly impact the lives of your people.\n" + "\n" + "We await your response.\n" + "\n" + "Sincerely,\n" + "%s";
	public static final CharSequence BUTTON_COUNCILOR = "Councilor?";
	
	public final String settlementName;
	public final String raiderName;
	
	public final CouncilorMessage councilorMessage;
	
	public RaidersMessage(
			CharSequence title, String settlementName, String raiderName, CouncilorMessage councilorMessage
	) {
		super(title);
		this.settlementName = settlementName;
		this.raiderName = raiderName;
		this.councilorMessage = councilorMessage;
	}
	
	/**
	 * @return The message body.
	 */
	@Override
	protected RENDEROBJ makeSection() {
		GuiSection section = new GuiSection();
		
		String formattedMessageBody = String.format(MESSAGE_BODY.toString(), settlementName, raiderName);
		
		GText body = new GText(UI.FONT().M, formattedMessageBody);
		
		section.add(body.setMaxWidth(WIDTH), 0, 0);
		
		section.addRelBody(48, DIR.S, new CouncilorButtPanel());
		
		return section;
	}
	
	private final class CouncilorButtPanel extends GButt.ButtPanel {
		CouncilorButtPanel() {
			super(BUTTON_COUNCILOR);
		}
		
		@Override
		protected void clickA() {
			super.clickA();
			close();
			councilorMessage.send();
		}
		
		@Override
		protected void renAction() {
			super.renAction();
			activeSet(true);
		}
	}
}
