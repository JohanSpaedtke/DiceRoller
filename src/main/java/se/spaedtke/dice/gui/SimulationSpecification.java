package se.spaedtke.dice.gui;

import java.util.Optional;

import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class SimulationSpecification extends JPanel{

	private final JTextField specification;
	
	public SimulationSpecification(String initialText){
		specification = new JTextField(30);
		specification.setText(initialText);
		specification.setMaximumSize(specification.getPreferredSize());
		add(specification);
	}
	
	public Optional<String> getDiceSpecification(){
		String potentialSpec = "";
		try{
			potentialSpec = specification.getText();
		}catch(NullPointerException e){
			//TextField was empty
		}
		return potentialSpec.isEmpty() ? Optional.empty() : Optional.of(potentialSpec);
	}
}
