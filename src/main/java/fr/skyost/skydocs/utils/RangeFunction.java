package fr.skyost.skydocs.utils;

import java.util.ArrayList;
import java.util.List;

import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import fr.skyost.skydocs.Constants;

/**
 * The range(start, end, step) function.
 */

public class RangeFunction extends SimpleJtwigFunction {
	
	@Override
	public final String name() {
		return Constants.FUNCTION_RANGE;
	}
	
	@Override
	public final Object execute(final FunctionRequest functionRequest) {
		try {
			if(functionRequest.getNumberOfArguments() < 2) {
				return "You must specify at least two arguments in the range function.";
			}
			final List<Object> arguments = functionRequest.getArguments();
					
			final int start = Integer.parseInt(arguments.get(0).toString());
			final int end = Integer.parseInt(arguments.get(1).toString());
			int step = 1;
			
			if(arguments.size() == 3) {
				step = Integer.parseInt(arguments.get(2).toString());
				if(step == 0) {
					throw new IllegalArgumentException("Step must not be 0.");
				}
			}

			if(start > end) {
				if(step > 0) {
					step = -step;
				}
				if(step < end) {
					throw new IllegalArgumentException("Step is too big.");
				}
			}
			else if(step > end) {
				throw new IllegalArgumentException("Step is too big.");
			}
			
			final List<Integer> result = new ArrayList<>();
			for(int i = start; i != end; i += step) {
				result.add(i);
			}
			
			return result;
		}
		catch(final IllegalArgumentException ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
}