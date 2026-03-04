import "./UpdateUser.css";
import { useEffect, useState } from "react";
import Button from 'react-bootstrap/Button';
import Form from "react-bootstrap/Form";

import { useNavigate, useParams } from "react-router-dom";


const UpdateUser = ()=>{

    const {id} =useParams();
    const navigate=useNavigate();

        const [formData,setFormData ] = useState ({
            name:"",
            email:"",
            phone:"",
            department:""
        })
    
    
        const handleInputChange = (event)=>{
    
            const {name,value}=event.target;
    
            setFormData({
                ...formData,
                [name]:value,
            })
        };

        useEffect(()=>{
            const fetchEmployees = async()=>{

                try{
                    const response = await fetch(`http://localhost:8080/api/employee/${id}`);

                    const data= await response.json();
                    setFormData(data);

                } catch(error){
                    console.error("Error fetching User: ",error.message);
                }
            }

            fetchEmployees();
        },[id])



        const handleSubmit = async (e) => {
            e.preventDefault();
    
            try {
                const response = await fetch(`http://localhost:8080/api/employee/${id}`, {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(formData),
                });
    
                if (!response.ok) {
                    throw new Error("Error updating user");
                }
    
                const data = await response.json();
                console.log("User Updated", data);
    
                // Redirect to the homepage after successful update
                navigate("/");
    
            } catch (error) {
                console.error("Error updating user:", error.message);
            }
        };


    return(


             <>


       <div className="center-form">
            <h1>Edit Employee</h1>

            <Form onSubmit={handleSubmit}>
                    <Form.Group controlId="FormBasicName">
                        <Form.Control type="text" name="name" value={formData.name} onChange={handleInputChange} placeholder="Name"/>
                    </Form.Group>

                    <Form.Group controlId="FormBasicEmail">
                        <Form.Control type="email" name="email" value={formData.email} onChange={handleInputChange}
                        placeholder="Email" />
                    </Form.Group>

                    <Form.Group controlId="FormBasicPhone">
                        <Form.Control type="text" name="phone" value={formData.phone} onChange={handleInputChange} 
                        placeholder="Phone"/>
                    </Form.Group>

                    <Form.Group controlId="FormBasicDepartment">
                        <Form.Control type="text" name="department" value={formData.department} onChange={handleInputChange} 
                        placeholder="Department"/>
                    </Form.Group>

                    <Button variant="primary" type="submit" className="w-100">Edit Employee</Button>
                </Form>

        </div> 




        </>
    )
}

export default UpdateUser;