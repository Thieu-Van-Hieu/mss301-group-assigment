import {Toaster} from "sonner";
import {AppRoutes} from "./routes/AppRoutes.tsx";

export default function App() {
	return (
		<div className="min-h-screen bg-gray-50 text-gray-900 antialiased">
			<AppRoutes/>
			<Toaster
				position="top-right"
				richColors
				expand={true}
				closeButton={true}
				duration={2000}
			/>
		</div>
	);
}