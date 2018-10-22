package gov.foia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.function.Predicate;

/**
 * A predicate that delegates the testing to an external JavaScript.
 * <p>
 * For example, it should be registered as:
 * <p>
 * <code>
 * <bean id="releaseFolderRule" class="java.io.File">
 * <constructor-arg
 * value="${user.home}/.arkcase/acm/foia-javascript-rules/releaseFolderRule.js"/>
 * </bean>
 * <p>
 * <bean id="folderNamePredicate" class="gov.foia.service.JavaScriptEvaluatingPredicate">
 * <property name="javaScript" ref="releaseFolderRule"/>
 * </bean>
 * </code>
 * <p>
 * The <code>JavaScript</code> should have define function named <code>evaluateCondition</code> that accepts single
 * argument of type <code>T</code>.
 * <p>
 * For example:
 * <p>
 * <code>
 * function evaluateCondition(acmFolder) {
 * return "01 Request" === acmFolder.getName();
 * }
 * </code>
 * <p>
 * In the example above type <code>T</code> is <code>AcmFolder</code>. The logic in the script can be of arbitrary
 * complexity. That is what makes thi predicate very flexible and versatile.
 * <p>
 * In the example above, the <code>JavaScript</code> is loaded from a file located at
 * <code>{user.home}/.arkcase/acm/foia-javascript-rules/releaseFolderRule.js</code>.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 22, 2016
 */
public class JavaScriptEvaluatingPredicate<T> implements Predicate<T>
{

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    private ScriptEngine engine;

    /**
     *
     */
    public JavaScriptEvaluatingPredicate()
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
    }

    /*
     * (non-Javadoc)
     * @see java.util.function.Predicate#test(java.lang.Object)
     */
    @Override
    public boolean test(T obj)
    {
        Invocable inv = (Invocable) engine;
        try
        {
            return (boolean) inv.invokeFunction("evaluateCondition", obj);
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            log.error("The script either does not have 'evaluateCondition' function defined or contains errors.", e);
            return false;
        }
    }

    /**
     * @param javaScript
     *            the javaScript to set
     * @throws FileNotFoundException
     * @throws ScriptException
     */
    public void setJavaScript(File javaScript) throws FileNotFoundException, ScriptException
    {
        FileReader reader = new FileReader(javaScript);
        engine.eval(reader);
    }

}
