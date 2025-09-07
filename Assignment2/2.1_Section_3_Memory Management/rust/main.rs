use std::{cell::RefCell, rc::Rc, thread, time::Duration};

#[derive(Debug)]
struct Node { next: RefCell<Option<Rc<Node>>> }

fn sum_owned(v: Vec<i32>) -> i32 { v.iter().sum() } // takes ownership

fn main() {
    // Borrow (no copy, no free calls from you)
    let v = vec![1, 2, 3];
    let borrowed_sum = v.iter().sum::<i32>();
    // Move ownership into function (v is moved)
    let owned_sum = sum_owned(v);
    println!("borrowed_sum={borrowed_sum}, owned_sum={owned_sum}");

    // INTENTIONAL STRONG CYCLE -> memory cannot be reclaimed via refcount
    let a = Rc::new(Node { next: RefCell::new(None) });
    let b = Rc::new(Node { next: RefCell::new(None) });
    *a.next.borrow_mut() = Some(Rc::clone(&b));
    *b.next.borrow_mut() = Some(Rc::clone(&a)); // cycle a<->b

    println!("a count={}, b count={}",
        Rc::strong_count(&a), Rc::strong_count(&b));

    // Pause so you can attach a profiler
    thread::sleep(Duration::from_secs(30));
}
