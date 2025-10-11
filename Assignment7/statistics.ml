(* Statistics Calculator - OCaml Functional Implementation *)

(* Compute arithmetic mean using functional approach *)
let compute_average numbers =
  let total = List.fold_left (fun accumulator element -> accumulator +. (float_of_int element)) 0.0 numbers in
  let length = float_of_int (List.length numbers) in
  total /. length

(* Determine middle value in sorted list *)
let determine_middle_value numbers =
  let ordered = List.sort compare numbers in
  let size = List.length ordered in
  if size mod 2 = 0 then
    let first_middle = List.nth ordered ((size / 2) - 1) in
    let second_middle = List.nth ordered (size / 2) in
    (float_of_int (first_middle + second_middle)) /. 2.0
  else
    float_of_int (List.nth ordered (size / 2))

(* Count occurrences of each element *)
let count_occurrences numbers =
  let rec occurrence_helper result = function
    | [] -> result
    | current :: remaining ->
      let occurrence_count = List.fold_left (fun counter element -> if element = current then counter + 1 else counter) 0 numbers in
      if List.mem_assoc current result then
        occurrence_helper result remaining
      else
        occurrence_helper ((current, occurrence_count) :: result) remaining
  in
  occurrence_helper [] numbers

(* Find highest occurrence count *)
let find_highest_count occurrence_list =
  List.fold_left (fun highest (_, count) -> max highest count) 0 occurrence_list

(* Identify most frequent values *)
let identify_most_frequent numbers =
  let occurrence_list = count_occurrences numbers in
  let highest_count = find_highest_count occurrence_list in
  let frequent_values = List.filter_map (fun (value, count) -> 
    if count = highest_count then Some value else None) occurrence_list in
  List.sort compare frequent_values

(* Function to print list *)
let print_list lst =
  let rec print_helper = function
    | [] -> print_string "[]"
    | [x] -> print_string ("[" ^ string_of_int x ^ "]")
    | x :: xs -> 
      print_string ("[" ^ string_of_int x);
      List.iter (fun y -> print_string (", " ^ string_of_int y)) xs;
      print_string "]"
  in
  print_helper lst

(* Function to print float list *)
let print_float_list lst =
  let rec print_helper = function
    | [] -> print_string "[]"
    | [x] -> print_string ("[" ^ string_of_float x ^ "]")
    | x :: xs -> 
      print_string ("[" ^ string_of_float x);
      List.iter (fun y -> print_string (", " ^ string_of_float y)) xs;
      print_string "]"
  in
  print_helper lst

(* Main function *)
let main () =
  print_endline "=== Statistics Calculator (OCaml - Functional) ===";
  print_newline ();
  
  (* Test data *)
  let data = [1; 2; 3; 4; 5; 2; 3; 2; 1; 3] in
  
  print_string "Input data: ";
  print_list data;
  print_newline ();
  print_newline ();
  
  (* Calculate and display mean *)
  let mean = compute_average data in
  print_string "Mean: ";
  print_float mean;
  print_newline ();
  
  (* Calculate and display median *)
  let median = determine_middle_value data in
  print_string "Median: ";
  print_float median;
  print_newline ();
  
  (* Calculate and display mode *)
  let modes = identify_most_frequent data in
  print_string "Mode: ";
  print_list modes;
  print_newline ();
  print_newline ()

(* Run the main function *)
let () = main ()
