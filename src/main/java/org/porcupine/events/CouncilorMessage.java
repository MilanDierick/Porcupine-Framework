/*
 * Copyright (c) 2022 Milan Dierick | This source file is licensed under a modified version of Apache 2.0
 */

package org.porcupine.events;

import game.faction.FACTIONS;
import init.sprite.UI.UI;
import settlement.main.SETT;
import settlement.stats.STATS;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GButt;
import util.gui.misc.GText;
import view.main.Message;
import world.World;

@SuppressWarnings({"HardcodedLineSeparator", "SerializableDeserializableClassInSecureContext", "SerializableHasSerializationMethods"})
public class CouncilorMessage extends Message {
	public static final CharSequence MESSAGE_BODY = "Chief, I have considered the ultimatum presented by the raider group. Based on my observations, the raiders do not seem to be well-trained or well-equipped, numbering around %s. While we may be able to put up a fight, there is a good chance we won't be able to defeat them. If we choose to resist, many of our people may be killed or injured, and we could lose valuable resources.\n" + "\n" + "However, if we surrender to them, there is a chance that they will be merciful and spare the lives of our people. While it is not guaranteed, it is worth considering as a potential outcome.\n" + "\n" + "I understand that surrendering is not an easy decision, but it may be the best course of action for the safety and well-being of our people and our settlement. Please consider this option carefully.";
	public static final CharSequence BUTTON_RESIST = "Resist!";
	public static final CharSequence BUTTON_SURRENDER = "Surrender";
	
	public final int raiderCount;
	
	public CouncilorMessage(CharSequence title, int raiderCount) {
		super(title);
		this.raiderCount = raiderCount;
	}
	
	/**
	 * @return The message body.
	 */
	@Override
	protected RENDEROBJ makeSection() {
		GuiSection section = new GuiSection();
		
		String formattedMessageBody = String.format(MESSAGE_BODY.toString(), raiderCount);
		
		GText body = new GText(UI.FONT().M, formattedMessageBody);
		
		section.add(body.setMaxWidth(WIDTH), 0, 0);
		
		section.addRelBody(48, DIR.S, new ResistButtPanel());
		section.addRelBody(4, DIR.S, new SurrenderButtPanel());
		
		return section;
	}
	
	private final class ResistButtPanel extends GButt.ButtPanel {
		ResistButtPanel() {
			super(BUTTON_RESIST);
		}
		
		@Override
		protected void clickA() {
			super.clickA();
			close();
		}
		
		@Override
		protected void renAction() {
			super.renAction();
			activeSet(true);
		}
	}
	
	private final class SurrenderButtPanel extends GButt.ButtPanel {
		SurrenderButtPanel() {
			super(BUTTON_SURRENDER);
		}
		
		@Override
		protected void clickA() {
			super.clickA();
			
			double totalSilver = STATS.GOVERN().RICHES.data().getD(null);
			double silverToPay = totalSilver * 0.6f;
			
			FACTIONS.player().credits().tribute.OUT.inc((int) silverToPay);
			SETT.ROOMS().STOCKPILE.removeFromEverywhere(0.6, -1l, FACTIONS.player().res().outTribute);
			World.ARMIES().rebels().all().get(0).disband();
			// TODO: Kill off a small percentage of the population.
			close();
		}
		
		@Override
		protected void renAction() {
			super.renAction();
			activeSet(true);
		}
	}
}
