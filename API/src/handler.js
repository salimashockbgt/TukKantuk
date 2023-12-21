import { nanoid } from 'nanoid';

import admin from 'firebase-admin';

import serviceAccount from '../serviceAccountKey.json' assert { type: "json" };

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://your-database-url.firebaseio.com"
  });
  
const db = admin.database();
  


const register = async (request, h) => {
    const { name, email, password } = request.payload;
    const userRef = db.ref('users');
    const existingUser = await userRef.orderByChild('email').equalTo(email).once('value');

    if (existingUser.exists()) {
        return h.response({ "error": true, "message": "User not Created. Email used." }).code(400);
    }

    const newUserRef = userRef.push();
    await newUserRef.set({ name, email, password });

    return h.response({ "error": false, "message": "User Created" }).code(200);
};


const login = (request, h) => {
    const {
        email, password
    } = request.payload;

    const userRef = db.ref('users');

    const userAccount = userRef.filter((users) => users.email == email)[0];

    if (userAccount === undefined || userAccount.password !== password){
        const response = h.response({
            "error": "true",
            "message": "Email not found or Wrong password"
        });
        response.code(400);
        return response;
    } else {
        userAccount.token = "tukkantuk";
        const response = h.response({
            "error": "false",
            "message": "success",
            "loginResult": userAccount.map((users) => ({
                userId : users.id,
                name : users.name,
                "token" : "tukkantuk"
            }))
        });
        response.code(200);
        return response;
    }
}

const stories = async (request, h) => {
    const {
        description, photo, lat, lon
    } = request.payload;

    const storyRef = db.ref('stories');

    if (!photo) {
        const response = h.response({
            "error": "true",
            "message": "No Photo"
        });
        response.code(400);
        return response;
    }
    const idStory = nanoid();

    const newStoryRef = storyRef.push();
    await newStoryRef.set({ idStory, description, photo, lat, lon});

    return h.response({ "error": false, "message": "success" }).code(200);
}


export { register, login, stories }