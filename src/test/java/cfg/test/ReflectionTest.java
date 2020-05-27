package cfg.test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ReflectionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Object v = Class.forName("cfg.test.RTest").newInstance();
			
			Method[] methods = v.getClass().getDeclaredMethods();
			
			for (Method m : methods) {

				   //if (!method.isBridge()) {
				       System.out.println(m.getName());
				  // }   
				       
				       Class<?>[] pType  = m.getParameterTypes();
						Type[] gpType = m.getGenericParameterTypes();
						for (int i = 0; i < pType.length; i++) {
							 System.out.println("ParameterType " + pType[i]);
							 System.out.println("GenericParameterType "+ gpType[i]);
						}
				}

			
		} catch (InstantiationException | IllegalAccessException	| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}		
		//Set<String> s = new HAshSet<String>();
		
		//Method validate = v.getClass().getDeclaredMethod("validate", String.class);
		//Method validColumns = v.getClass().getDeclaredMethod("validColumns", Set<String>);
		
		/**
		Method[] methods = v.getClass().getDeclaredMethods();

		for (Method method : methods) {

		   //if (!method.isBridge()) {
		       System.out.println(method.getName());
		  // }   
		}


	}

}
*/

class RTest	{
	
	private Long val;

	public Long getVal() {
		return val;
	}

	public void setVal(Long val) {
		this.val = val;
	}
	
}