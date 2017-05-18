package com.murauyou.channels;

import com.murauyou.channels.client.Subscriber;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class DefaultCompositeChannelUnitTest {

    /**
     * Subscriber that should fail the Unit test as should not have ben triggered
     */
    private static final Subscriber FAILING_SUBSCRIBER = new Subscriber() {
        @Override
        public void onTrigger(Object eventMsg) {
            fail("This subscriber should not have been triggered.");
        }
    };

    private CompositeChannel parent;

    private CompositeChannel child1;
    private CompositeChannel child2;

    private CompositeChannel child1_1;
    private CompositeChannel child1_2;
    private CompositeChannel child2_1;
    private CompositeChannel child2_2;

    private CompositeChannel child1_1_1;
    private CompositeChannel child1_1_2;
    private CompositeChannel child1_2_1;
    private CompositeChannel child1_2_2;
    private CompositeChannel child2_1_1;
    private CompositeChannel child2_1_2;
    private CompositeChannel child2_2_1;
    private CompositeChannel child2_2_2;

    /**
     * Building tree of composite subscribers by implicitly adding channel children via 'get'
     */
    @Before
    public void setupChildrenByImplicitAdding() {
        parent = new DefaultCompositeChannel("path");

        child1 = parent.get("left");
        child2 = parent.get("right");

        child1_1 = child1.get("alpha");
        child1_2 = child1.get("beta");
        child2_1 = child2.get("gamma");
        child2_2 = child2.get("delta");

        child1_1_1 = child1_1.get("london");
        child1_1_2 = child1_1.get("manchester");
        child1_2_1 = child1_2.get("liverpool");
        child1_2_2 = child1_2.get("leeds");
        child2_1_1 = child2_1.get("newcastle");
        child2_1_2 = child2_1.get("york");
        child2_2_1 = child2_2.get("birmingham");
        child2_2_2 = child2_2.get("cardiff");
    }

    /**
     * Testing path normalization
     */
    @Test
    public void testPathNomalization() {
        CompositeChannel parent = new DefaultCompositeChannel("path");

        CompositeChannel child1 = parent.get("left");
        CompositeChannel child2 = parent.get("not&left");

        CompositeChannel child1_1 = child1.get("EVENT:NUmbER:1");
        CompositeChannel child1_2 = child1.get("beta");
        CompositeChannel child2_1 = child2.get("gamma");
        CompositeChannel child2_2 = child2.get("delta");

        CompositeChannel child1_1_1 = child1_1.get("london");
        CompositeChannel child1_1_2 = child1_1.get("manchester");
        CompositeChannel child1_2_1 = child1_2.get("liverpool");
        CompositeChannel child1_2_2 = child1_2.get("leeds");
        CompositeChannel child2_1_1 = child2_1.get("newcastle");
        CompositeChannel child2_1_2 = child2_1.get("york");
        CompositeChannel child2_2_1 = child2_2.get("birmingham.insider:polish");
        CompositeChannel child2_2_2 = child2_2.get("cardiff");

        assertEquals("path.left.event_number_1.london", child1_1_1.path());
        assertEquals("path.left.beta.leeds", child1_2_2.path());
        assertEquals("path.not&left.delta.birmingham_insider_polish", child2_2_1.path());
        assertEquals("path.not&left.delta.cardiff", child2_2_2.path());
    }

    @Test
    public void testBasicPathSetup() {
        assertEquals("path.left.alpha.london", child1_1_1.path());
        assertEquals("path.left.beta.leeds", child1_2_2.path());
        assertEquals("path.right.delta.birmingham", child2_2_1.path());
        assertEquals("path.right.delta.cardiff", child2_2_2.path());
    }

    /**
     * Publishing event on 'child2' should only trigger subscribers of 'parent' and 'child2'
     */
    @Test
    public void testTreeLevel2WithImplicitChildAdding() {
        setupChildrenByImplicitAdding();

        Subscriber parentSubscriber = mock(Subscriber.class);
        parent.subscribe(parentSubscriber);

        Subscriber child2Subscriber = mock(Subscriber.class);
        child1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2.subscribe(child2Subscriber);

        child1_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered

        child1_1_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_1_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_2_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_2_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_1_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_1_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_2_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_2_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered

        child2.publish("message");

        Mockito.verify(parentSubscriber, times(1)).onTrigger("message");
        Mockito.verify(child2Subscriber, times(1)).onTrigger("message");
    }

    /**
     * Publishing event on 'child1_2_2' should trigger the chain of subscribers: 'parent' -> 'child1' -> 'child1_2' -> 'child1_2_2'
     */
    @Test
    public void testTreeLevel4WithImplicitChildAdding() {
        setupChildrenByImplicitAdding();

        Subscriber parentSubscriber = mock(Subscriber.class);
        parent.subscribe(parentSubscriber);

        Subscriber child1Subscriber = mock(Subscriber.class);
        child1.subscribe(child1Subscriber);
        child2.subscribe(FAILING_SUBSCRIBER); // should not be triggered

        Subscriber child1_2Subscriber = mock(Subscriber.class);
        child1_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_2.subscribe(child1_2Subscriber);
        child2_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered

        Subscriber child1_2_2Subscriber = mock(Subscriber.class);
        child1_1_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_1_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_2_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child1_2_2.subscribe(child1_2_2Subscriber);
        child2_1_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_1_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_2_1.subscribe(FAILING_SUBSCRIBER); // should not be triggered
        child2_2_2.subscribe(FAILING_SUBSCRIBER); // should not be triggered

        child1_2_2.publish("message");

        Mockito.verify(parentSubscriber, times(1)).onTrigger("message");
        Mockito.verify(child1Subscriber, times(1)).onTrigger("message");
        Mockito.verify(child1_2Subscriber, times(1)).onTrigger("message");
        Mockito.verify(child1_2_2Subscriber, times(1)).onTrigger("message");
    }

}
